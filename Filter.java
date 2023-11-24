import java.util.List;
import java.util.ArrayList;
public class Filter {
    private Criterion criterian;

    public Filter(Criterion criterian) {
        this.criterian = criterian;
    }
    public List<Session> filter(List<Session> sessions) {
        List<Session> result = new ArrayList<>();
        for (Session session : sessions) {
            if (criterian.validdate(session)) {
                result.add(session);
            }
        }
        return result;
    }
}