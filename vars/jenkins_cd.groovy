def hostname() {
  sh '''hostname -f'''
}

def sftp_get() {
  withCredentials([sshUserPrivateKey(credentialsId:'sftp-key', keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME}"
  }
}
