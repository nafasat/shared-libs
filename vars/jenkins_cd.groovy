def hostname() {
  sh '''hostname -f'''
}

def sftp_get(Map sftp_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    dir("${WORKSPACE}/")
    {
      if ("${sftp_args.target_path}" == '') {
        sftp_args.target_path='.'
      }
      sh "echo get ${sftp_args.target_path}/${sftp_args.tar_archive_name} | sshpass -p ${PASSWORD} sftp -q -oStrictHostKeyChecking=no ${USERNAME}@${sftp_args.sftp_ip}"
    }
  }
}

def push_github_script(Map github_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    loadGitHubScript(name: 'push_github.sh')
    sh "./push_github.sh ${USERNAME} ${PASSWORD} ${github_args.commit_msg} ${github_args.archive_name} ${github_args.repo_name_without_https}"
  }
}

def image_push_to_quay_repo(Map quay_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${quay_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    sh "skopeo login --username ${USERNAME} --password ${PASSWORD} enterprisequay.hbctxdom.com"
    sh "skopeo copy docker-archive:./${quay_args.archive_name} docker://enterprisequay.hbctxdom.com/${quay_args.container_repo}/${quay_args.container_image_name}:${quay_args.image_tag}"
  }
}

def scan_and_get_report(Map quay_scan_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${quay_scan_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
  
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
