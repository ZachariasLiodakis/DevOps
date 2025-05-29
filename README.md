# DevOps - Πληροφοριακό Σύστημα Κατανεμημένης Υποδομής

Αυτή η εργασία υλοποιεί ένα πληροφοριακό σύστημα βασισμένο σε μικροϋπηρεσίες με χρήση Spring Boot και PostgreSQL, το οποίο υποστηρίζεται από Jenkins, Ansible, Docker και Kubernetes. Η αρχιτεκτονική είναι πλήρως αυτοματοποιημένη και μπορεί να γίνει deploy σε περιβάλλον VM, Docker ή Kubernetes.

## 🔧 Τεχνολογίες & Εργαλεία

- **Backend**: Spring Boot (Java)
- **Database**: PostgreSQL
- **Admin Panel**: Spring Boot Admin
- **Email Service**: Mailhog (για testing)
- **CI/CD**: Jenkins + Jenkinsfile ανά component
- **Containerization**: Docker & Docker Compose
- **Configuration Management**: Ansible
- **Orchestration**: Kubernetes (MicroK8s)

## 📁 Δομή Monorepo

- `Akinita` → Spring Boot application (real estate listings)
- `ansible` → Όλα τα playbooks για την εγκατάσταση των components
- `Email-service` → Περιέχει τα Dockerfiles και τα Jenkinsfile για το deployment
- `k8s` → Απαραίτητα έγγραφα για το kubernetes

## 🚀 Οδηγίες Εκκίνησης

### 1. Ανάπτυξη με Ansible σε VM
```bash
cd DevOps/ansible/
ansible-playbook -i hosts.yaml playbooks/spring.yaml -e "vm_ip=YOUR_VM_IP"
```

### 2.1 Τοπική Ανάπτυξη με Docker Compose

```bash
cd devops
docker-compose up --build
```

### 2.2 Ανάπτυξη με Docker Compose και Anisble σε VM
```bash
cd DevOps/ansible/
ansible-playbook -i hosts.yaml playbooks/deploy-compose.yaml -e "vm_ip=YOUR_VM_IP"
```
