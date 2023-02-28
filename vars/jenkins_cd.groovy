/**
 * sftp_get - function to retrieve a file from an SFTP server
 * @param sftp_args: a Map object containing the following keys:
 *        credential_sftp_name: the ID of the Jenkins credentials containing the SFTP username and password
 *        sftp_path: the path on the SFTP server where the file is located
 *        tar_archive_name: the name of the file to retrieve
 *        sftp_ip: the IP address or hostname of the SFTP server
 * @return none
 */
def sftp_get(Map sftp_args = [:]) {
  // Use withCredentials to retrieve the SFTP username and password from Jenkins credentials
  withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    // Change to the Jenkins workspace directory
    dir("${WORKSPACE}/") {
      // If the sftp_path is not provided, set it to the current directory (".")
      if ("${sftp_args.sftp_path}" == '') {
        sftp_args.sftp_path='.'
      }
      // Use the "sh" step to execute an SFTP command to retrieve the file
      sh "echo get ${sftp_args.sftp_path}/${sftp_args.tar_archive_name} | sshpass -p ${PASSWORD} sftp -q -oStrictHostKeyChecking=no ${USERNAME}@${sftp_args.sftp_ip}"
    }
  }
}

/**
 * push_github_script - function to push files to a GitHub repository
 * @param github_args: a Map object containing the following keys:
 *        credential_github_name: the ID of the Jenkins credentials containing the GitHub username and password
 *        commit_msg: the commit message to use when pushing changes to the repository. If not provided, the Jenkins build tag is used as the commit message.
 *        archive_name: the name of the archive file to push to the repository
 *        repo_name_without_https: the name of the repository to push the files to, without the https:// prefix
 *        push_to_feature_branch_name: the name of the feature branch to push the files to
 *        pull_from_branch_name: the name of the branch to pull changes from before pushing the files
 * @return none
 */


def push_github_script(Map github_args = [:]) {
  // Use withCredentials to retrieve the GitHub username and password from Jenkins credentials
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    // If the commit message is not provided, set it to the Jenkins build tag
    if ("${github_args.commit_msg}" == '') {
      github_args.commit_msg="${BUILD_TAG}"
    }
    // Load the push_github.sh script
    loadGitHubScript(name: 'push_github.sh')
    // Execute the push_github.sh script with the necessary arguments
    sh "./push_github.sh ${USERNAME} ${PASSWORD} ${github_args.commit_msg} ${github_args.archive_name} ${github_args.repo_name_without_https} ${github_args.push_to_feature_branch_name} ${github_args.pull_from_branch_name}"
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
