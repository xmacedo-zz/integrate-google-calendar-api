package xmacedo.enums;

public enum EStatusReuniao {
    ABERTA("Aberta"), CANCELADA("Cancelada"), REALIZADA ("Realizada");

    private String descricao;

    EStatusReuniao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
