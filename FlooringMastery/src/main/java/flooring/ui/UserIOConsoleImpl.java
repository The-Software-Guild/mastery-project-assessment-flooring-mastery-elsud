package flooring.ui;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserIOConsoleImpl implements UserIO {
    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String readString(String prompt) {
        System.out.println(prompt);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();

    }

    @Override
    public int readInt(String prompt) {
        while (true) {
            try {
                String stringInput = this.readString(prompt);
                return Integer.parseInt(stringInput);
            } catch (NumberFormatException e) {
                print("Input error. Please try again");
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        int input;
        do {
            input = this.readInt(prompt);
        } while (input < min || input > max);
        return input;
    }

    @Override
    public double readDouble(String prompt) {
        while (true) {
            try {
                String stringInput = this.readString(prompt);
                return Double.parseDouble(stringInput);
            } catch (NumberFormatException e) {
                print("Input error. Please try again");
            }
        }
    }

    @Override
    public double readDouble(String prompt, double min, double max) {
        double input;
        do {
            input = this.readDouble(prompt);
        } while (input < min || input > max);
        return input;
    }

    @Override
    public float readFloat(String prompt) {
        while (true) {
            try {
                String stringInput = this.readString(prompt);
                return Float.parseFloat(stringInput);
            } catch (NumberFormatException e) {
                print("Input error. Please try again");
            }
        }
    }

    @Override
    public float readFloat(String prompt, float min, float max) {
        float input;
        do {
            input = this.readFloat(prompt);
        } while (input < min || input > max);
        return input;
    }

    @Override
    public long readLong(String prompt) {
        while (true) {
            try {
                String stringInput = this.readString(prompt);
                return Long.parseLong(stringInput);
            } catch (NumberFormatException e) {
                print("Input error. Please try again");
            }
        }
    }

    @Override
    public long readLong(String prompt, long min, long max) {
        long input;
        do {
            input = this.readLong(prompt);
        } while (input < min || input > max);
        return input;
    }
}
