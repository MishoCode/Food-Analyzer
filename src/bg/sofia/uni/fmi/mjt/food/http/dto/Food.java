package bg.sofia.uni.fmi.mjt.food.http.dto;

import java.io.Serializable;
import java.util.Objects;

public class Food implements Serializable {
    private int fdcId;
    private String description;
    private String gtinUpc;

    public Food() {
    }

    public Food(int fdcId, String description, String gtinUpc) {
        this.fdcId = fdcId;
        this.description = description;
        this.gtinUpc = gtinUpc;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return fdcId == food.fdcId && Objects.equals(description, food.description) &&
               Objects.equals(gtinUpc, food.gtinUpc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fdcId, description, gtinUpc);
    }

    @Override
    public String toString() {
        return "Food{" +
               "fdcId:" + fdcId +
               ", description:'" + description + '\'' +
               ", gtinUpc:'" + gtinUpc + '\'' +
               '}';
    }

    public String getGtinUpc() {
        return gtinUpc;
    }
}
