<table>
  <tr>
    <td>
      <img src="https://github.com/ZachariasLiodakis/DevOps/blob/main/Deployment.png?raw=true" alt="DevOps" height="100" />
    </td>
    <td style="vertical-align: middle;">
      <h1 style="font-size: 1em; margin: 0;">DevOps – Distributed Infrastructure Information System</h1>
    </td>
  </tr>
</table>

This project implements an information system ([Akinita](https://github.com/ZachariasLiodakis/Akinita)) based on microservices using Spring Boot and PostgreSQL, supported by Jenkins, Ansible, Docker and Kubernetes. The architecture is fully automated and can be deployed in a VM, Docker or Kubernetes environment. 

## 🔧 Technologies & Tools

- **Backend**: Spring Boot (Java)
- **Database**: PostgreSQL
- **Admin Panel**: Spring Boot Admin
- **Email Service**: Spring Boot (Java,Tomcat)
- **CI/CD**: Jenkins
- **Containerization**: Docker & Docker Compose
- **Configuration Management**: Ansible
- **Orchestration**: Kubernetes (MicroK8s)
- **ΝΧ**: Monorepo structure

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
ansible-playbook -i ansible/hosts.yaml ansible/playbooks/spring.yaml ansible/playbooks/email.yaml -e "vm_ip=YOUR_VM_IP"
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

  Step 1 ( install jenkins ):

  [Jenkins](https://www.jenkins.io/doc/book/installing/linux/)
  
  Step 2 ( enable Jenkins ):
  ```bash
    sudo systemctl enable jenkins
  ```
  Step 3:

  Once Jenkins is configured. Create credentials 'Secret Text'

    k8s_vm_ip_creds	
    jenkins_user	
    jenkins_token	
    jenkins_ip

  Step 4 ( Create keys on your jenkins and k8s Vms ):

  On both of the Vms run:
  ```bash
  ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
  ```

  Be sure you name your ssh-keys id_rsa...
  Next add each key on the other Vm's ssh-keys-file ( auhtorisedkeys, knownhosts etc.)

  Step 5:

  Create a pipeline job in Jenkins with this repo, and the jenkinsFile in the directory k8s/JenkinsFile.
  You are ready to Build.
  
## ⚙️ Application Access
- For accessing the application via VM: http://YOUR_VM_IP:8080
- For accessing the application locally: http://localhost:8080
