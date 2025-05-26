pipeline {

agent any

environment {
    DOCKER_TOKEN=credentials('docker-push-secret')
    DOCKER_USER= 'zachariasliodakis'
    DOCKER_SERVER= 'ghcr.io'
    DOCKER_PREFIX='ghcr.io/zachariasliodakis/Akinita'
    }

stages {

    stage('Test') {
        steps {
            sh '''
                echo "Start testing"
                cd Akinita/Akinita
                ./mvnw test
                ''' 
        }
    }

    stage('Docker build and push') {
        steps {
            sh '''
                HEAD_COMMIT=$(git rev-parse --short HEAD)
                TAG=$HEAD_COMMIT-$BUILD_ID
                docker build --rm -t $DOCKER_PREFIX:$TAG -t $DOCKER_PREFIX:latest -f Akinita/Dockerfile .
            '''
            sh '''
                echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                docker push $DOCKER_PREFIX --all-tags
            '''
        }
    }
}
}