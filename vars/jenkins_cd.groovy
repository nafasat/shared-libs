def hostname() {
  sh '''hostname -f'''
}

def sftp_get(String credential_store_name, String target) {
  withCredentials([sshUserPrivateKey(credentialsId:"${credential_store_name}", keyFileVariable: 'keyfile',usernameVariable: 'USERNAME')]) {
    sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME} ${target}"
  }
}

def hello(String name) {
  echo "My Name is ${name}"
}
