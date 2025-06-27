# DevOps - Distributed Infrastructure Information System

This project implements an information system based on microservices using Spring Boot and PostgreSQL, supported by Jenkins, Ansible, Docker and Kubernetes. The architecture is fully automated and can be deployed in a VM, Docker or Kubernetes environment.

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
  a. Set up your mainApp-host Vm, installed with snap, microk8s, kubectl
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
    
    
    
  b. Install Jenkins on another Vm. ( we suggest using jdk-21 )
  c. Extract from the mainApp-host the 
## ⚙️ Application Access
- For accessing the application via VM: http://YOUR_VM_IP:8080
- For accessing the application locally: http://localhost:8080
