def sftp_get(Map sftp_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    dir("${WORKSPACE}/")
    {
      if ("${sftp_args.sftp_path}" == '') {
        sftp_args.sftp_path='.'
      }
      sh "echo get ${sftp_args.sftp_path}/${sftp_args.tar_archive_name} | sshpass -p ${PASSWORD} sftp -q -oStrictHostKeyChecking=no ${USERNAME}@${sftp_args.sftp_ip}"
    }
  }
}

def push_github_script(Map github_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    if ("${github_args.commit_msg}" = '')
    {
      github_args.commit_msg=${BUILD_TAG}
    }
    loadGitHubScript(name: 'push_github.sh')
    sh "./push_github.sh ${USERNAME} ${PASSWORD} ${github_args.commit_msg} ${github_args.archive_name} ${github_args.repo_name_without_https} ${github_args.push_to_feature_branch_name} ${github_args.push_to_feature_branch_name} ${github_args.pull_from_branch_name}"
  }
}

def image_push_to_quay_repo(Map quay_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${quay_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    sh "skopeo login --username ${USERNAME} --password ${PASSWORD} enterprisequay.hbctxdom.com"
    sh "skopeo copy docker-archive:./${quay_args.archive_name} docker://enterprisequay.hbctxdom.com/${quay_args.container_repo}/${quay_args.container_image_name}:${quay_args.image_tag} && sleep 10"
  }
}

def scan_and_get_report(Map quay_scan_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${quay_scan_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    def quay_url='enterprisequay.hbctxdom.com'
    def report_file='report_file.txt'
    def vulnerablities_file='vulnerablities_file.out'
    def manifest = sh returnStdout: true, script: "skopeo inspect docker://${quay_url}/${quay_scan_args.quay_work_space}/${quay_scan_args.Image_repo_name}:${quay_scan_args.Image_tag_name} | jq -r .Digest"
    int count = 0;
    while(count<10) {
      def image_scane_status = sh returnStdout: true, script: "curl -s --user ${USERNAME}:${PASSWORD} https://${quay_url}/api/v1/repository/${quay_scan_args.quay_work_space}/${quay_scan_args.Image_repo_name}/mainifest${mainifest_name}/security?vulnerabilities=true | jq .status"
      if ( image_scane_status == '"status"' ) {
        break
      } else {
        if (count == 10) {
          println("TimeOut, Scanning process may be still in progress, Please check manually")
          currentBuild.result = 'ABORTED'
          error("Aborting the build.")
        } else {
          count++
         }
      }
    }
    sh "curl -s --user ${USERNAME}:${PASSWORD} https://${quay_url}/api/v1/repository/${quay_scan_args.quay_work_space}/${quay_scan_args.Image_repo_name}/mainifest${mainifest_name}/security?vulnerabilities=true > ${report_file}"
    sh "jq -r '.data.Layer.Features[].vulnerabilities[] | select (.Severity == ("High") or .Severity == ("Medium") or .Severity == ("Critical")) | .Severity' ${report_file} | sort | uniq -c > ${vulnerablities_file}"
    def Critical_Vul = sh returnStdout: true, script: "awk '/Critical/{print\$1}' ${vulnerablities_file}"
    def High_Vul = sh returnStdout: true, script: "awk '/High/{print\$1}' ${vulnerablities_file}"
    def Medium_Vul = sh returnStdout: true, script: "awk '/Medium/{print\$1}' ${vulnerablities_file}"
    if (Critical_Vul>1) {
      println(Critical)
    } else if (High_Vul>1) {
        println(High_Vul)
    } else if (Medium_Vul>1) {
      println(Medium_Vul)
    } else {
      println("No Vul")
    }    
  }
}
