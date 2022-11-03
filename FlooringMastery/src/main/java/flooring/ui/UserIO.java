package flooring.ui;

public interface UserIO {

    /**
     * Display the message
     *
     * @param message
     */
    void print(String message);

    /**
     * Display the prompt message, waits for the user input to return
     *
     * @param prompt message to display
     * @return user's input
     */
    String readString(String prompt);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into integer
     *
     * @param prompt message to display
     * @return integer that was entered by user
     */
    int readInt(String prompt);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into integer. Repeats until integer wouldn't be
     * in the range from min to max
     *
     * @param prompt message to display
     * @param min minimum value for entered integer
     * @param max maximum value for entered integer
     * @return integer that was entered by user
     */
    int readInt(String prompt, int min, int max);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into double
     *
     * @param prompt message to display
     * @return double that was entered by user
     */
    double readDouble(String prompt);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into double. Repeats until double wouldn't be
     * in the range from min to max
     *
     * @param prompt message to display
     * @param min minimum value for entered double
     * @param max maximum value for entered double
     * @return double that was entered by user
     */
    double readDouble(String prompt, double min, double max);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into float
     *
     * @param prompt message to display
     * @return float that was entered by user
     */
    float readFloat(String prompt);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into float. Repeats until float wouldn't be
     * in the range from min to max
     *
     * @param prompt message to display
     * @param min minimum value for entered float
     * @param max maximum value for entered float
     * @return float that was entered by user
     */
    float readFloat(String prompt, float min, float max);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into long
     *
     * @param prompt message to display
     * @return long that was entered by user
     */
    long readLong(String prompt);

    /**
     * Display the prompt message and waits for user's input.
     * Modify that input into long. Repeats until long wouldn't be
     * in the range from min to max
     *
     * @param prompt message to display
     * @param min minimum value for entered long
     * @param max maximum value for entered long
     * @return long that was entered by user
     */
    long readLong(String prompt, long min, long max);

}