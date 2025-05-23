package Akinita.project.Akinita.Services;

import Akinita.project.Akinita.Entities.Actors.Renter;
import Akinita.project.Akinita.Repositories.User.RenterRepository;
import Akinita.project.Akinita.Repositories.User.RoleRepository;
import Akinita.project.Akinita.Repositories.User.UserRepository;
import Akinita.project.Akinita.Entities.Role;
import Akinita.project.Akinita.Entities.Actors.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository; //Δήλωση του user repository
    @Autowired
    private RoleRepository roleRepository; //Δήλωση του role repository
    @Autowired
    private RenterRepository renterRepository; //Δήλωση του renter repository
    private BCryptPasswordEncoder passwordEncoder; //Δήλωση του password Encoder
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private RenterService renterService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User saveUser(User user, String assignedrole) { //Μέθοδος αποθήκευσης user
        String passwd= user.getPassword(); //Ανάκτηση password
        String encodedPassword = passwordEncoder.encode(passwd); //Κωδικοποίηση password
        user.setPassword(encodedPassword); //Προσθήκη password

        Role role = roleRepository.findByName(assignedrole)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(role); //Προσθήκη ρόλου στο HashSet
        user.setRoles(roles); //Προθήκη ρόλου στον user
        return userRepository.save(user); //Αποθήκευση στη βάση δεδομένων
    }

    @Transactional
    public Integer updateUser(User user) { //Ενημέρωση user
        user = userRepository.save(user); //Αποθήκευση στη βάση δεδομένων
        return user.getId(); //Επιστροφή Id
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //Φόρτωση του user από τη βάση δεδομένων
        Optional<User> opt = userRepository.findByUsername(username);

        if (opt.isEmpty()) { //Στην περίπτωση που ΔΕΝ υπάρχει το username
            throw new UsernameNotFoundException("User with email: " + username + " not found!");
        } else { //Στην περίπτωση που υπάρχει το username
            User user = opt.get();

            //Έλεγχος αν ο χρήστης έχει τον ρόλο ROLE_RENTER
            boolean isRenter = user.getRoles().stream()
                    .anyMatch(role -> role.toString().equals("ROLE_RENTER"));

            if (isRenter) {
                //Φόρτωση του Renter από το RenterRepository
                Optional<Renter> renterOpt = renterRepository.findById(user.getId());

                if (renterOpt.isEmpty() || !renterOpt.get().getAcceptance().equals("Accepted")) { //Έλεγχος αν ο Renter είναι αποδεκτός από τον admin
                    throw new UsernameNotFoundException("Renter with email: " + username + " is not accepted!");
                }
            }

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.toString()))
                            .collect(Collectors.toSet())
            );
        }
    }

    @Transactional
    public void deleteUser(User user, int id) {
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_RENTER"))) {  // Διαγραφή από τον αντίστοιχο πίνακα του role
            renterService.deleteRenter(id);
        }
        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_OWNER"))){
            ownerService.deleteOwner(id);
        }
        // Διαγραφή του user
        userRepository.delete(user);
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(int userId) {
        return userRepository.findById(userId).get();
    }

    public boolean existsUser(String username) {
        return userRepository.existsByUsername(username);
    }

    public Object getRoles() {
        return roleRepository.findAll();
    }

    public Object getRole(Integer roleId) {
        return roleRepository.findById(roleId).get();
    }

    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
