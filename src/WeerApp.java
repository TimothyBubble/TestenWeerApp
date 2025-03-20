import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeerApp {
    private static final String API_KEY = System.getenv("WEATHER_API_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    private JFrame frame;
    private JTextField cityInput;
    private JButton getWeatherButton;
    private JLabel weatherLabel;

    public WeerApp() {
        frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,200);
        frame.setLayout(new FlowLayout());

        cityInput = new JTextField(15);
        getWeatherButton = new JButton("Get weather");
        weatherLabel = new JLabel("Enter a city and press the button.");

        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityInput.getText().trim();
                if (!city.isEmpty()) {
                    String weather = getWeather(city);
                    weatherLabel.setText("<html>" + weather.replaceAll("\\n", "<br>") + "</html");
                }
            }
        });

        frame.add(cityInput);
        frame.add(getWeatherButton);
        frame.add(weatherLabel);

        frame.setVisible(true);
    }

    private String getWeather(String city) {
        try {
            if (API_KEY == null || API_KEY.isEmpty()) {
                return "Error: API key is missing. Set WEATHER_API_LEY environment variable.";
            }

            String urlString = BASE_URL + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
            String description = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");

            return "Temperature: " + temperature + "°C\n" + "Description " + description;

        }   catch (Exception e) {
            return "Error: Unable to fetch weather";
        }
    }

    public static void main(String[] args) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.out.println("Error: API key is missing. Set WEATHER_API_KEY environment variable.");
            return;
        }

        SwingUtilities.invokeLater(() -> new WeerApp());
    }
}