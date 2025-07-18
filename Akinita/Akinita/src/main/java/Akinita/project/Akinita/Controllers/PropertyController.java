package Akinita.project.Akinita.Controllers;

import Akinita.project.Akinita.Entities.Actors.User;
import Akinita.project.Akinita.Entities.Enums.EnergyClass;
import Akinita.project.Akinita.Entities.Enums.Facilities;
import Akinita.project.Akinita.Entities.Properties.Property;
import Akinita.project.Akinita.Services.EmailClientService;
import Akinita.project.Akinita.Services.PropertyService;
import Akinita.project.Akinita.Services.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import Akinita.project.Akinita.Entities.Properties.House;
import Akinita.project.Akinita.Entities.Properties.CommercialProperty;
import Akinita.project.Akinita.Entities.Properties.Parking;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Akinita.project.Akinita.Entities.Enums.Facilities.*;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("Service")
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @Autowired
    EmailClientService emailClientService;
    @Autowired
    private UserService userService;

    @GetMapping("/searchProperties")
    public String searchProperties(@RequestParam(name = "location", required = false) String location,
                                   @RequestParam(name = "propertyType", required = false) String propertyType,
                                   @RequestParam(name = "minPrice", required = false) Double minPrice,
                                   @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                                   @RequestParam(name = "priceSlider", required = false) Double priceSlider,
                                   @RequestParam(name = "minSize", required = false) Integer minSize,
                                   @RequestParam(name = "maxSize", required = false) Integer maxSize,
                                   @RequestParam(name = "sizeSlider", required = false) Integer sizeSlider,
                                   @RequestParam(name = "buildingFees", required = false) Boolean buildingFees,
                                   @RequestParam(name = "constructionDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date constructionDate,
                                   @RequestParam(name = "facilities", required = false) List<String> facilitiesRaw,
                                   @RequestParam(name = "energyClass", required = false) String energyClassRaw,
                                   RedirectAttributes redirectAttributes) {

        String locationT = (location.isEmpty())? "All" : location;

        Boolean availability = true;

        EnergyClass energyClass = null;

        if(energyClassRaw != null) {
            energyClass = switch (energyClassRaw) {
                case "Apls" -> EnergyClass.Apls;
                case "A" -> EnergyClass.A;
                case "Bpls" -> EnergyClass.Bpls;
                case "B" -> EnergyClass.B;
                case "C" -> EnergyClass.C;
                case "D" -> EnergyClass.D;
                case "E" -> EnergyClass.E;
                case "Z" -> EnergyClass.Z;
                case "H" -> EnergyClass.H;
                case "EMPTY" -> EnergyClass.EMPTY;
                default -> energyClass;
            };
        }

        List<Facilities> facilities = new ArrayList<>();

        if (facilitiesRaw != null) {
            if(facilitiesRaw.contains("All")){
                facilities.add(ALL);


            } else {
                for (String fc : facilitiesRaw) {

                    switch (fc) {
                        case "AC":
                            facilities.add(AC);
                            break;

                        case "Elevator":
                            facilities.add(ELEVATOR);
                            break;

                        case "Parking":
                            facilities.add(PARKING);
                            break;

                        case "Garden":
                            facilities.add(GARDEN);
                            break;

                        case "Fireplace":
                            facilities.add(FIREPLACE);
                            break;

                        case "Pool":
                            facilities.add(POOL);
                            break;

                        case "Storage":
                            facilities.add(STORAGE);
                            break;

                        case "Alarm":
                            facilities.add(ALARM);
                            break;
                    }
                }
            }
        }


        List<Property> properties = propertyService.findProperties(locationT, propertyType, minPrice, maxPrice,
                                                                    minSize, maxSize, buildingFees, constructionDate,priceSlider,
                                                                    sizeSlider, availability,facilities,energyClass);

        redirectAttributes.addFlashAttribute("properties", properties);
        return "redirect:/Service/search_results";  // Επιστρέφει την αντίστοιχη σελίδα αποτελεσμάτων
    }

    @GetMapping("/search_results")
    public String RenterSearchResults(Model model) {
        // Λήψη των αποτελεσμάτων από τα FlashAttributes
        List<Property> properties = (List<Property>) model.asMap().get("properties");

        // Προσθήκη των αποτελεσμάτων στο μοντέλο για την προβολή στη σελίδα
        model.addAttribute("properties", properties);
        return "search_results";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/AcceptListings") //Μέθοδος προβολής ιδιοκτησιών προς αποδοχή από τον admin
    public String updateProperties(Model model){
        model.addAttribute("properties", propertyService.findAllInvisibleProperties()); //Προσθήκη ιδιοκτησιών στο model
        return "properties/updateProperties";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @GetMapping("/AcceptListings/{property_id}") //Μέθοδος αποδοχής ιδιοκτησίας από τον admin
    public String updateProperties(@ModelAttribute("properties") Property property,@PathVariable int property_id) throws MessagingException {
        Property the_property= propertyService.getPropertyById(property_id);
        User owner = userService.getUser(the_property.getOwnerId());
        the_property.setVisibility("Visible"); //Αλλαγή visibility

        emailClientService.sendEmail(owner.getEmail(), "Your property has been accepted!","Your property: " + the_property.getEstateName() +  " has been successfully reviewed and accepted by the admin.");


        try {
            propertyService.updateProperty(the_property); //Αποθήκευση αλλαγών στη Βάση Δεδομένων
        }catch (Exception e){
            throw new RuntimeException("Could not update property");
        }

        return "redirect:/Service/AcceptListings";

    }

    @GetMapping("/ListingInfo/{property_id}") //Μέθοδος αποδοχής ιδιοκτησίας από τον admin
    public String PropertyInfo(@ModelAttribute("properties") Property property,@PathVariable int property_id,Model model){
        Property the_property= propertyService.getPropertyById(property_id);
        List<Facilities> facilities = propertyService.getPropertyFacilitiesById(property_id);
        model.addAttribute("the_property", the_property);
        model.addAttribute("facilities", facilities);
        // Έλεγχος τύπου property και προσθήκη των ειδικών πεδίων στο Model
        if (the_property instanceof House house) {
            model.addAttribute("constructionDate", house.getConstructionDate());
            model.addAttribute("buildingFees", house.getBuildingFees());
            model.addAttribute("energyClass", house.getEnergyClass());
        } else if (the_property instanceof Parking parking) {
            model.addAttribute("constructionDate", parking.getConstructionDate());
            model.addAttribute("buildingFees", parking.getBuildingFees());
            model.addAttribute("energyClass", parking.getEnergyClass());
        } else if (the_property instanceof CommercialProperty commercial) {
            model.addAttribute("constructionDate", commercial.getConstructionDate());
            model.addAttribute("buildingFees", commercial.getBuildingFees());
            model.addAttribute("facilities", commercial.getFacilities());
        }
        return "properties/propertyInformation";
    }
}