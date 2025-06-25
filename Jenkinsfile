pipeline {

agent any

environment {
    DOCKER_TOKEN=credentials('docker-push-secret')
    DOCKER_USER= 'zachariasliodakis'
    DOCKER_SERVER= 'ghcr.io'
    DOCKER_PREFIX='ghcr.io/zachariasliodakis/devops'
    }

stages {

    stage('Test') {
        steps {
            sh '''
                echo "Start testing the Akinita app"
                cd Akinita/Akinita
                chmod +x mvnw
                ./mvnw test
                '''
        }
    }

    stage('Docker build and push') {
        steps {
            script {
                def headCommit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                def tag = "${headCommit}-${env.BUILD_ID}"

                // === Build Akinita App ===
                sh """
                    docker build --rm -t $DOCKER_PREFIX/akinita:$tag -t $DOCKER_PREFIX/akinita:latest -f Akinita/Dockerfile Akinita/
                """

                // === Build EmailService App ===
                sh """
                    docker build --rm -t $DOCKER_PREFIX/emailservice:$tag -t $DOCKER_PREFIX/emailservice:latest -f EmailService/Dockerfile EmailService/
                """

                // === Docker Login & Push All ===
                sh """
                    echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                    docker push $DOCKER_PREFIX/akinita --all-tags
                    docker push $DOCKER_PREFIX/emailservice --all-tags
                """
            }
        }
    }

}
}