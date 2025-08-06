package sk.atos.fri.dao;

public enum LogTypeStatus {
  CreateUser(1), UpdateUser(2), CaseChangeStatus(3), TechnicalLog(4);

  public final Long id;

  LogTypeStatus(long id) {
    this.id = id;
  }

}
