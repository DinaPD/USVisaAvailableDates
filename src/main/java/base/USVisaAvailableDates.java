package base;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class USVisaAvailableDates {
    private final static LocalDate currentAppointment = LocalDate.of(2024, 02, 05);

    private WebDriver driver;
    private String password;
    private String login;

    public USVisaAvailableDates(String login, String password) {
        this.login = login;
        this.password = password;

    }

    public void setUp() throws IOException {
        var webDriverPath = System.getenv("WEB_DRIVER");
        if (webDriverPath == null) {
            webDriverPath = ".jenkins/workspace/test/resources/chromedriver";
        }
        System.out.println("WebDriver will be used '%s'".formatted(webDriverPath));
        System.setProperty("webdriver.chrome.driver", webDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        //driver.manage().window().minimize();
        driver.get("https://ais.usvisa-info.com/en-ca/niv/users/sign_in");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        runTest(wait);
    }

    public void runTest(WebDriverWait wait) {
        inputEmail(wait);
        inputPassword(wait);
        inputCheckBox(wait);
        clickSignIn(wait);
        clickContinueFirst(wait);
        rescheduleAppointment(wait);
        rescheduleAppointmentButtonClick(wait);
        clickContinue(wait);
        //dateInput(wait);
        AuthenticationDetails authenticationDetails = getUserAgentAndPartOfTheCookies();
        List<AvailableDay> listOfDates = getListOfDates(authenticationDetails);
        compareCurrentAppointmentDates(listOfDates);
    }

    public void inputEmail(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email")));
        WebElement inputEmail = driver.findElement(By.id("user_email"));
        inputEmail.click();
        inputEmail.sendKeys(login);
    }

    public void inputPassword(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement inputPassword = driver.findElement(By.id("user_password"));
        inputPassword.click();
        inputPassword.sendKeys(password);
    }

    public void inputCheckBox(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("icheckbox")));
        WebElement inputCheckBox = driver.findElement(By.className("icheckbox"));
        inputCheckBox.click();
    }

    public void clickSignIn(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button")));
        WebElement signIn = driver.findElement(By.className("button"));
        signIn.click();
    }

    public void clickContinueFirst(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main\"]/div[2]/div[2]/div[1]/div/div/div[1]/div[2]/ul/li/a")));
            WebElement clickContinue = driver.findElement(By.xpath("//*[@id=\"main\"]/div[2]/div[2]/div[1]/div/div/div[1]/div[2]/ul/li/a"));
            clickContinue.click();
        } catch (Exception e) {
            exit(e, "Invalid login or password. Could not sign in. Try again.");
        }
    }

    public void rescheduleAppointment(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Reschedule Appointment")));
        WebElement rescheduleAppointment = driver.findElement(By.linkText("Reschedule Appointment"));
        rescheduleAppointment.click();
    }

    public void rescheduleAppointmentButtonClick(WebDriverWait wait) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='button small primary small-only-expanded'] [@href='/en-ca/niv/schedule/44624449/appointment']")));
        WebElement appointment = driver.findElement(By.xpath("//a[@class='button small primary small-only-expanded'] [@href='/en-ca/niv/schedule/44624449/appointment']"));
        appointment.click();
    }

    public void clickContinue(WebDriverWait wait) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main\"]/div[3]/form/div[2]/div/input")));
        WebElement continueSecond = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/form/div[2]/div/input"));
        continueSecond.click();
    }

    public void dateInput(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("appointments_consulate_appointment_date_input")));
            WebElement dateInput = driver.findElement(By.id("appointments_consulate_appointment_date_input"));
            dateInput.click();
        } catch (Exception e) {
            exit(e, "There are no available appointments at the selected location. Please try again later");
        }
    }

    private void exit(Exception e, String message) {
        System.out.println("Error occurred '" + e.getMessage() + "'");
        e.printStackTrace();
        displayNotification(message);
        driver.close();
        System.exit(1);
    }

    public AuthenticationDetails getUserAgentAndPartOfTheCookies() {
        Set<Cookie> cookie = driver.manage().getCookies();
        System.out.println(cookie);
        var ua = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        var yatriToken = cookie.stream().filter(cookie1 -> cookie1.getName().equals("_yatri_session")).findFirst().get();

        return new AuthenticationDetails(yatriToken, ua);
    }

    public List<AvailableDay> getListOfDates(AuthenticationDetails authenticationDetails) {
        return HttpClient.getAvailableDates(authenticationDetails.getAuthCookie().toString(), authenticationDetails.getUserAgent());
    }


    public void compareCurrentAppointmentDates(List<AvailableDay> availableDates) {


        if (availableDates.size() > 0) {

            var firstDate = LocalDate.parse(availableDates.get(0).getDate());

            if (firstDate.isBefore(currentAppointment)) {
                String message = "The nearest date is: %s".formatted(firstDate) + " " + "Is earlier then current appointment";
                displayNotification(message);
                displayNotification("URA URA URA !");
                displayNotification("URA URA URA !");
                displayNotification("URA URA URA !");

                try {
                    Thread.sleep(9999999);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }else {
                String message = "The nearest date is: %s".formatted(firstDate) + " " + "Is later then current appointment";
                displayNotification(message);
            }


        } else {
            displayNotification("There are no available appointments at the selected location. Please try again later");

        }
    }

    public void displayNotification(String message) {
        try {
            Runtime.getRuntime().exec(new String[]{"osascript", "-e", "display notification \"" + message + "\" with title \"Title\""});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Login or password is not set");
           System.exit(1);
        }

        USVisaAvailableDates tests = new USVisaAvailableDates(args[0], args[1]);
        tests.setUp();


    }
}
