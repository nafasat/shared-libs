def hostname() {
  sh '''hostname -f'''
}

def sftp_get(Map sftp_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    sh "echo get ${sftp_args.tar_archive_name} | sshpass -p ${PASSWORD} sftp -q -oStrictHostKeyChecking=no ${USERNAME}@${sftp_args.sftp_ip}"
  }
}

def push_github(Map github_args = [:])
{
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    dir("${WORKSPACE}/${github_args.target}")
    {
      sh '''
      git clone https://github.com/nafasat/testing_git.git
      echo "${github_args.tar_archive_name}"
      '''
    }
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
