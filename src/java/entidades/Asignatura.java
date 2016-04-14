package entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Aaron
 */
@Entity
@Table(name = "asignatura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asignatura.findAll", query = "SELECT a FROM Asignatura a"),
    @NamedQuery(name = "Asignatura.findByCodasignatura", query = "SELECT a FROM Asignatura a WHERE a.codasignatura = :codasignatura"),
    @NamedQuery(name = "Asignatura.findByNombreAsignatura", query = "SELECT a FROM Asignatura a WHERE a.nombreAsignatura = :nombreAsignatura"),
    @NamedQuery(name = "Asignatura.findByEstado", query = "SELECT a FROM Asignatura a WHERE a.estado = :estado")})
public class Asignatura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codasignatura")
    private String codasignatura;
    @Basic(optional = false)
    @Column(name = "nombre_asignatura")
    private String nombreAsignatura;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codasignatura")
    private List<Horario> horarioList;

    public Asignatura() {
    }

    public Asignatura(String codasignatura) {
        this.codasignatura = codasignatura;
    }

    public Asignatura(String codasignatura, String nombreAsignatura, int estado) {
        this.codasignatura = codasignatura;
        this.nombreAsignatura = nombreAsignatura;
        this.estado = estado;
    }

    public String getCodasignatura() {
        return codasignatura;
    }

    public void setCodasignatura(String codasignatura) {
        this.codasignatura = codasignatura;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<Horario> getHorarioList() {
        return horarioList;
    }

    public void setHorarioList(List<Horario> horarioList) {
        this.horarioList = horarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codasignatura != null ? codasignatura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asignatura)) {
            return false;
        }
        Asignatura other = (Asignatura) object;
        if ((this.codasignatura == null && other.codasignatura != null) || (this.codasignatura != null && !this.codasignatura.equals(other.codasignatura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Asignatura[ codasignatura=" + codasignatura + " ]";
    }

}
