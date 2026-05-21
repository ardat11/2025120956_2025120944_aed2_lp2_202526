package models;

import java.time.LocalDate;

/**
 * Represents a platform user with a specific region and registration date.
 */
public class User extends Entity implements Comparable<User> {
    private String region;
    private LocalDate registrationDate;

    /**
     * Constructs a new User.
     *
     * @param id               the unique identifier for the user
     * @param name             the name of the user
     * @param region           the geographical region of the user
     * @param registrationDate the date the user registered on the platform
     */
    public User(String id, String name, String region, LocalDate registrationDate) {
        super(id, name);
        this.region = region;
        this.registrationDate = registrationDate;
    }

    // Getters
    public String getRegion() {
        return region;
    }
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public int compareTo(User other)
    {
        if (other == null || other.getId() == null) {
            return 1;
            // Pushes the null objects to the end to prevent Null Pointer Exception

        }
        if (this.id == null) {
            return -1;
        }
        return this.id.compareTo(other.getId());
    }
}