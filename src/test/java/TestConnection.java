import edu.usc.cs.db.hw5.model.Lion;
import edu.usc.cs.db.hw5.model.Pond;
import edu.usc.cs.db.hw5.model.Region;
import edu.usc.cs.db.hw5.services.DbService;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by tg on 11/24/15.
 */
public class TestConnection {

    public static void main(String[] args) throws SQLException {
        DbService instance = DbService.getInstance();

        Iterator<Lion> lions = instance.getAll(Lion.class);
        Iterable<Lion> l = () -> lions;
        for (Lion lion : l) {
            System.out.println(lion.getId() + "::" + lion.getGeom());
            //System.out.println(Arrays.toString(lion.getGeom().getJavaPoints()));
        }

        Iterator<Pond> ponds = instance.getAll(Pond.class);
        Iterable<Pond> ps = () -> ponds;
        for (Pond p : ps) {
            System.out.println(p.getId() + "::" + p.getGeom());
            //System.out.println(Arrays.toString(lion.getGeom().getJavaPoints()));
        }

        Iterator<Region> regions = instance.getAll(Region.class);
        Iterable<Region> rs = () -> regions;
        for (Region r : rs) {
            System.out.println(r.getId() + "::" + r.getGeom());
            //System.out.println(Arrays.toString(lion.getGeom().getJavaPoints()));
        }
    }
}
