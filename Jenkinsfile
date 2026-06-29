pipeline {
  agent any // can be run on any node/runner
  tools {
    maven 'Maven38'
  }
  stages {
    stage('Clone') { steps {
        git branch: 'lab09', url: 'https://github.com/Chin-Hongnyheng/Automated-testing.git'
    }}
    stage('Build')  { steps { sh 'mvn clean package' } }
    stage('Test')   { steps { sh 'echo "test the project"' } }
    stage('Package'){ steps { sh 'echo "Package the project"' } }
  }
  post {
    always  { junit '**/target/surefire-reports/*.xml' }
    success { echo '✔ Pipeline green' }
    failure { echo '✗ Build failed' }
  }
}