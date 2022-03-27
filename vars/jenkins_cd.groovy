def hostname() {
  sh '''hostname -f'''
}

def sftp_get(Map sft_args = [:]) {
  withCredentials([sshUserPrivateKey(credentialsId:"${sft_args.credential_sftp_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME} ${sft_args.target}"
    sh "echo ${sft_args.tar_archive_name}"
  }
}

def push_github(Map github_args = [:])
{
  withCredentials([sshUserPrivateKey(credentialsId:"${github_args.credential_github_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    dir("${WORKSPACE}/${github_args.target}")
    {
      sh "tar -xf ${github_args.tar_archive_name}"
      sh "rm -rf ${github_args.tar_archive_name}"
      actual_file_name = "${github_args.tar_archive_name}".replaceAll(".zip","")
      sh "echo ${actual_file_name}"
    }
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
