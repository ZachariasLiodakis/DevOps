# DevOps - Distributed Infrastructure Information System

This project implements an information system ([Akinita](https://github.com/ZachariasLiodakis/Akinita)) based on microservices using Spring Boot and PostgreSQL, supported by Jenkins, Ansible, Docker and Kubernetes. The architecture is fully automated and can be deployed in a VM, Docker or Kubernetes environment. 

## 🔧 Technologies & Tools

- **Backend**: Spring Boot (Java)
- **Database**: PostgreSQL
- **Admin Panel**: Spring Boot Admin
- **Email Service**: Spring Boot (Java,Tomcat)
- **CI/CD**: Jenkins + Jenkinsfile per component
- **Containerization**: Docker & Docker Compose
- **Configuration Management**: Ansible
- **Orchestration**: Kubernetes (MicroK8s)

## 📁 Monorepo Structure

- `Akinita` → Spring Boot application (real estate listings)
- `ansible` → All playbooks for installing the components
- `Email-service` → Contains the EmailService Project 
- `k8s` → Contains all the deployments for the different components

## 🎥 Video Demos

### Deployment with Ansible and Jenkins
  [Ansible + Jenkins](https://youtu.be/jS-u3wXZ59Y)

### Deployment Using Ansible, Docker and Jenkins
  [Ansible + Docker + Jenkins](https://youtu.be/DldWStTcoOI)

### Deployment using Microk8s, Ansible and Jenkins
  [Ansible + Microk8s + Jenkins](https://youtu.be/n4xYRxjVICY)

## 🚀 Startup Instructions

### 1. Deployment with Ansible on VM
```bash
ansible-playbook -i ansible/hosts.yaml ansible/playbooks/spring.yaml -e "vm_ip=YOUR_VM_IP"
```

### 2.1 Local Deployment with Docker Compose

```bash
cd /path/to/DevOps
docker-compose up --build
```

### 2.2 Deployment with Docker Compose and Ansible on VM
```bash
ansible-playbook -i ansible/hosts.yaml ansible/playbooks/deploy-compose.yaml -e "vm_ip=YOUR_VM_IP"
```
### 3. Deployment with Jenkins and Kubernetes
`Set up your mainApp-host Vm, installed with snap, microk8s, kubectl`

Step 1 ( installing microk8s ):
  ```bash
    sudo snap install microk8s --classic
  ```
  Step 2 ( allowing routes ):
  ```bash
    sudo ufw allow in on ethh0 && sudo ufw allow out on eth0
    sudo ufw default allow routed    
  ```
  Step 3 ( configuring ./kube dir ):
  ```bash
    sudo usermod -a -G microk8s $USER   ( needs exiting and re-entering the vm )
    mkdir ~/.kube
    sudo chown -f -R $USER ~/.kube
    sudo su - $USER
  ```
  Step 4 ( enabling services ):
  ```bash
    microk8s.enable dns storage ingress
  ```
  Step 5 ( setting up the kubeconfig ):
  ```bash
    microk8s.kubectl config view --raw > ~/.kube/config
  ```
  Step 6:
  
  Copy the config and save it locally on your computer.

  Step 5:

  Edit the kubeconfig file, entering the mainApp-host-Vm's ip on the server block.
  Delete the line 'certificate-authority-data:".."' under the  " cluster: " field
  Enter a new line
  ```bash
    insecure-skip-tls-verify: true
  ```
  Basically ignoring the self signed certificates.
  
`Install Jenkins on another Vm`

  Step 1 ( enter the jenkins-Vm and install java ):
  ```bash
    sudo apt install fontconfig openjdk-21-jre
    sudo apt install openjdk-21-jdk
  ```
  Step 2 ( install jenkins ):
  
  [Jenkins](https://www.jenkins.io/doc/book/installing/linux/)
  
  Step 3 ( enable Jenkins ):
  ```bash
    sudo systemctl enable jenkins
  ```
  Step 4:

  Once Jenkins is configured. Create a credential 'Secret File' with the kubeconfig file saved locally.
  Name it ```"kubeconfig-creds"```.

  `Create the Pipeline job in Jenkins`

  Final Step:

  Create a pipeline job in Jenkins with this repo, and the jenkinsFile in the directory k8s/JenkinsFile.
  You are ready to Build.
  
## ⚙️ Application Access
- For accessing the application via VM: http://YOUR_VM_IP:8080
- For accessing the application locally: http://localhost:8080
