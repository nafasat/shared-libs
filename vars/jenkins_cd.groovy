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
  withCredentials([sshUserPrivateKey(credentialsId:"${github_args.credential_github_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    dir("${WORKSPACE}/${github_args.target}")
    {
      //sh "tar -xf ${github_args.tar_archive_name}"
      //sh "rm -rf ${github_args.tar_archive_name}"
      actual_file_name = "${github_args.tar_archive_name}".replaceAll(".zip","")
      sh "echo ${actual_file_name}"
    }
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
