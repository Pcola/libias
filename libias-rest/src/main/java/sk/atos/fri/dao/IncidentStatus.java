package sk.atos.fri.dao;

public enum IncidentStatus {
  Open(1), FilesDoublet(2), FilesNoDoublet(3), NotClear(4), NoProcessing(5), FilesNoLink(6), Adjusted(7), ReadyToQA(8), DNumberDiff(9),AutoAdjusted(10);

  public final int id;

  IncidentStatus(int id) {
    this.id = id;
  }

}
