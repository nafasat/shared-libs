def hostname() {
  sh '''hostname -f'''
}

def sftp_get() {
  sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME}"
}
