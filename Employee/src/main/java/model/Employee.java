package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.*;

@Entity
@Table(name = "Employee")

public class Employee {
    @Id
    private int EmpID;
    @Column
    private int ProjectID;
    @Column
    private LocalDate dateFrom;
    @Column
    private LocalDate dateTo;
    @Column
    private Long daysWorked;

    public Employee(int empID, int projectID, LocalDate dateFrom, LocalDate dateTo, Long daysWorked) {
        EmpID = empID;
        ProjectID = projectID;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.daysWorked = daysWorked;
    }

    public int getEmpID() {
        return EmpID;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Long getDaysWorked() {
        return daysWorked;
    }
}
