package cf.vozhuo.app.broswser.favorites;

public interface FavoritesController {
    void modify(FavHisEntity favorites);
    void delete(FavHisEntity favorites);
    void add(FavHisEntity favorites);
}
