def hostname() {
  sh '''hostname -f'''
}

def sftp_get(string credential_store_name) {
  withCredentials([sshUserPrivateKey(credentialsId:${credential_store_name}, keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME}"
  }
}
