package src.control;

/**
 * Generates unique IDs for entities.
 *
 * @author SC2002 Group
 * @version 1.0
 */
public class IdGenerator {
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * Constructor for IdGenerator
     * 
     * @param internshipRepository  internship repository for counting
     * @param applicationRepository application repository for counting
     */
    public IdGenerator(InternshipRepository internshipRepository,
            ApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Generate unique internship ID
     * Format: INT000001, INT000002, etc.
     * 
     * @return unique internship ID
     */
    public String generateInternshipID() {
        return "INT" + String.format("%06d", internshipRepository.size() + 1);
    }

    /**
     * Generate unique application ID
     * Format: APP000001, APP000002, etc.
     * 
     * @return unique application ID
     */
    public String generateApplicationID() {
        return "APP" + String.format("%06d", applicationRepository.size() + 1);
    }
}
