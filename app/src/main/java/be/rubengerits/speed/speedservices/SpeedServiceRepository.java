package be.rubengerits.speed.speedservices;

import java.util.HashMap;
import java.util.Map;

public class SpeedServiceRepository {

    private static final SpeedServiceRepository INSTANCE = new SpeedServiceRepository();

    private Map<String, SpeedService> speedServices;

    private SpeedServiceRepository() {
    }

    public static SpeedServiceRepository getInstance() {
        return INSTANCE;
    }

    public void registerService(SpeedService service) {
        if (speedServices == null) {
            speedServices = new HashMap<>();
        }
        speedServices.put(service.getId(), service);
    }

    public SpeedService getSpeedService(String id) {
        return speedServices.get(id);
    }

}