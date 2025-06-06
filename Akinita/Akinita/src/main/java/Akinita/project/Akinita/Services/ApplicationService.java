package Akinita.project.Akinita.Services;

import Akinita.project.Akinita.Entities.RentalApplication;
import Akinita.project.Akinita.Repositories.RentalApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class ApplicationService {

    @Autowired
    private RentalApplicationRepository rentalApplicationRepository;

    public List<RentalApplication> findByOwner(int owner_id) {
        return rentalApplicationRepository.findByOwnerId(owner_id);
    }

    public List<RentalApplication> findByRenter(int renter_id) {
        return rentalApplicationRepository.findByRenterId(renter_id);
    }

    public void deleteApplication(int appId) {
         rentalApplicationRepository.deleteById(appId);
    }

    public void deleteByRenterId(int renter_id) {
        rentalApplicationRepository.deleteByRenterId(renter_id);
    }

    public void save(RentalApplication rentalApplication) {
        rentalApplicationRepository.save(rentalApplication);
    }

    public void setDateCurrDate(int applicationId) {
        Date currDate = new Date();
        rentalApplicationRepository.setDateCurrDate(applicationId, currDate);
    }

    public List<RentalApplication> findAllApps(Integer renterId){
        return rentalApplicationRepository.findAllApps(renterId);
    }

    public RentalApplication findById(int applicationId) {
        return rentalApplicationRepository.findById(applicationId);
    }

    public void setApplicationStatus(int applicationId, Boolean status) {
        RentalApplication rentalApplication = rentalApplicationRepository.findById(applicationId);
        rentalApplication.setStatus(status);
        rentalApplicationRepository.save(rentalApplication);
    }
}
