def hostname() {
  sh '''hostname -f'''
}

def sftp_get(Map config = [:]) {
  withCredentials([sshUserPrivateKey(credentialsId:"${config.credential_sftp_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME} ${config.target}"
    sh "echo ${config.tar_archive_name}"
  }
}

def push_github(String credential_github_name, String repo_path, String comment, String target, String tar_archive_name)
{
  withCredentials([sshUserPrivateKey(credentialsId:"${credential_github_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    dir("${WORKSPACE}/${target}")
    {
      // sh "tar -xf $tar_archive_name"
      // sh "rm -rf $tar_archive_name"
      actual_file_name = "${tar_archive_name}".replaceAll(".tar.gz","")
      echo "${actual_file_name}"
    }
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
