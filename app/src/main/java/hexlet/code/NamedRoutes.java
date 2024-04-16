package hexlet.code;

public class NamedRoutes {

    public static String rootPath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlsChecksPath(String id) {
        return "/urls/" + id + "/checks";
    }

    public static String urlsChecksPath(Long id) {
        return urlsChecksPath(String.valueOf(id));
    }

}
