def hostname() {
  sh '''hostname -f'''
}

def sftp_get(Map sftp_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    sh "echo get ${sftp_args.target_path}/${sftp_args.tar_archive_name} | sshpass -p ${PASSWORD} sftp -q -oStrictHostKeyChecking=no ${USERNAME}@${sftp_args.sftp_ip}"
  }
}

def push_github(Map github_args = [:])
{
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    dir("${WORKSPACE}/${github_args.target}")
    {
      sh "echo ${github_args.archive_name}"
    }
  }
}


def push_github_script(Map config = [:]) {
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    loadGitHubScript(name: 'push_github.sh')
    sh "./push_github.sh ${USERNAME} ${PASSWORD} ${config.commit_msg} ${config.archive_name} ${config.repo_name_without_https}"
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
