package net.iGap.model.bill;

public class Debit<T> {

    private T data;

    public Debit() {
    }

    public Debit(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
