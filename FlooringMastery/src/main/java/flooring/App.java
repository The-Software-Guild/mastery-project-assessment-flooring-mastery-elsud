package flooring;

import flooring.controller.FlooringMasteryController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan("flooring");
        applicationContext.refresh();
        FlooringMasteryController controller = applicationContext.getBean(
                "controller", FlooringMasteryController.class);
        controller.run();
    }
}