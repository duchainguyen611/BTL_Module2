package businessImp;

import java.util.List;

public interface IBusiness <T1,T2>{
    List<T1 > findAll();

    boolean create(T1 t);

    boolean updateInfor(T1 t);

    boolean updateStatus(T1 t);

    List<T1> search(T2 t);
}
