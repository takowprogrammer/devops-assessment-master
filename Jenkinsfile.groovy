pipeline {
    agent any
    
    tools {
        jdk 'JDK11'
        maven 'Maven3'
        git 'Git'
        ansible 'Ansible'
     }
    
    environment {
        JAVA_HOME = tool name: 'JDK11', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
        DOCKER_CREDENTIALS_ID = 'dGFrb3d0YWtvdzpSU0JnaEVTSDNYd1BzLzg='
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'build branch')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def branch = params.BRANCH
                    git url: 'https://github.com/takowprogrammer/devops-assessment-master.git', branch: branch
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvnHome = tool name: 'Maven3'
                    sh "'${mvnHome}/bin/mvn' compile"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool name: 'Maven3'
                    sh "'${mvnHome}/bin/mvn' test"
                }
            }
        }

        stage('Run') {
            steps {
                script {
                    def mvnHome = tool name: 'Maven3'
                    sh "'${mvnHome}/bin/mvn' exec:java"
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    docker.build("takowtakow/test-repo/devops-assessment:latest")
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        docker.image("takowtakow/test-repo/devops-assessment:latest").push()
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                ansiblePlaybook colorized: true, installation: 'Ansible', playbook: 'deploy.yaml'
            }
        }
    }
}
