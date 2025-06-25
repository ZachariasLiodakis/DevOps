package Akinita.project.Akinita.Controllers;

import Akinita.project.Akinita.Entities.*;
import Akinita.project.Akinita.Entities.Actors.Renter;
import Akinita.project.Akinita.Entities.Actors.User;
import Akinita.project.Akinita.Entities.Properties.Property;
import Akinita.project.Akinita.Services.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("Renter")
public class RenterController {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private RenterService renterService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private EmailClientService emailClientService;

    @GetMapping("/rental_application")
    public String RenterRentalApplications(Model model, @RequestParam("property_id") Property property) {
        model.addAttribute("property_id", property.getId());
        return "renter/rental_application";
    }

    @PostMapping("/rental_application")
    public String submitRentalApp(@RequestParam("property_id") Integer property_id,
                                  @RequestParam("jobSituation") String jobSituation,
                                  @RequestParam("rentalDuration") int rentalDuration,
                                  @RequestParam("description") String description,
                                  @RequestParam(value = "pets", defaultValue = "false") boolean pets,
                                  Model model, Principal principal, RedirectAttributes redirectAttributes) throws MessagingException {

        String email = principal.getName();
        Integer renterId = renterService.findRenterIdByEmail(email);
        User renter = userService.getUser(renterId);

        Property property = propertyService.getPropertyById(property_id);
        User owner = userService.getUser(property.getOwnerId());                // Βρες τον Owner απο το Property

        // Δημιουργία του αντικειμένου αίτησης ενοικίασης
        RentalApplication application = new RentalApplication();

        List<RentalApplication> applicationServices = applicationService.findByRenter(renter.getId());
        for (RentalApplication applicationService : applicationServices) {
            if (Objects.equals(applicationService.getProperty().getId(), property.getId())) {
                redirectAttributes.addFlashAttribute("isError", true);
                redirectAttributes.addFlashAttribute("errorMessage", "Έχετε ήδη υποβάλει αίτηση για αυτό το ακίνητο.");
                return "redirect:/"; // Η σελίδα των αποτελεσμάτων αναζήτησης
            }
        }

        application.setRenter(renter);
        application.setProperty(property);
        application.setOwner(owner);

        application.setRenterJob(jobSituation);
        application.setRentalDuration(rentalDuration);
        application.setDescription(description);
        application.setRenterPets(pets);

        // Αποθήκευση της αίτησης (μπορείς να την αποθηκεύσεις στη βάση μέσω του Service)
        renterService.saveApplication(application);

        // Προαιρετικά, προσθέτουμε ένα μήνυμα επιτυχίας για την αναγνώριση της επιτυχίας της αποθήκευσης
        model.addAttribute("message", "Η αίτηση σας καταχωρήθηκε επιτυχώς.");

        emailClientService.sendEmail(renter.getEmail(),"Renter Registration Submission of " + renter.getUsername(),"Your rental application has been submitted and sent by email to the owner!\n" +
                "Please wait while they review it...");
        emailClientService.sendEmail(application.getOwner().getEmail(), "The user: "+ renter.getUsername() + " wants to rent your property!",
                "The user: "+ renter.getUsername() + " wants to rent your property!  ( " + application.getProperty().getEstateName() + " ) \n Please enter to the website to review the application!");    // ideally a link would be provided but it's not needed for now

        // Ανακατεύθυνση ή επιστροφή στη σελίδα
        return "redirect:/Renter/applicationSubmitted";  // Ανακατεύθυνση στη σελίδα με τις αιτήσεις
    }

    @GetMapping("/registrationSubmitted")
    public String RegistrationApplicationsSub() {
        return "renter/registrationsubmitted";
    }

    @GetMapping("/applicationSubmitted")
    public String RentalApplicationsSub() {
        return "renter/applicationSubmitted";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/AcceptRenters") //Μέθοδος προβολής Renters προς αποδοχή από τον admin
    public String AcceptRenters(Model model) {
        model.addAttribute("Renters",renterService.findAllUnacceptedRenters()); //Προσθήκη Renters στο model
        return "renter/acceptRenters";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @GetMapping("/AcceptRenters/{renter_id}") //Μέθοδος αποδοχής Renter
    public String acceptRenters(Model model, @PathVariable Integer renter_id) throws MessagingException {
        Renter the_renter = renterService.getRenterById(renter_id);
        the_renter.setAcceptance("Accepted"); //Αλλαγή απο "Unaccepted" σε "Accepted"
        try{
            renterService.UpdateRenter(the_renter); //Αποθήκευση αλλαγής στη Βάση Δεδομένων
        }catch (Exception e){
            throw new RuntimeException("Could not update renter");
        }

        emailClientService.sendEmail(the_renter.getUser().getEmail(), "Your renter account has been accepted!","Your renter account has been successfully reviewed and accepted by the admin. \n You can now freely explore what our website has to offer!");
        return "redirect:/Renter/AcceptRenters";
    }

    @GetMapping("/RentalApplications")
    public String RentalApplications(Model model, Principal principal) {

        String email = principal.getName();
        Integer renterId = renterService.findRenterIdByEmail(email);

        model.addAttribute("RentalApplications",applicationService.findAllApps(renterId));
        return "renter/application_numbers";
    }

    @PostMapping("/RentalApplications/{rentalApplication_id}")
    public String deleteApplications(@PathVariable("rentalApplication_id") Integer applicationId,RedirectAttributes redirectAttributes) {
        try {

            applicationService.deleteApplication(applicationId);

            redirectAttributes.addFlashAttribute("isSuccess", true);
            redirectAttributes.addFlashAttribute("success", "Application deleted successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("isError", true);
            redirectAttributes.addFlashAttribute("error", "Error while deleting application");
        }


        return "redirect:/Renter/RentalApplications"; // Redirect to the same page after deletion
    }

    @GetMapping("/RentedProperties")
    public String RentedProperties(Model model, Principal principal) {
        String email = principal.getName();
        Integer renterId = renterService.findRenterIdByEmail(email);
        model.addAttribute("Properties", propertyService.findPropertiesByRenterId(renterId));
        return "renter/rentedProperties";
    }
 }
