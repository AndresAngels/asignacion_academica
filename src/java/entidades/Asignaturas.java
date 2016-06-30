package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Aaron
 */
@Entity
@Table(name = "asignaturas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asignaturas.findAll", query = "SELECT a FROM Asignaturas a"),
    @NamedQuery(name = "Asignaturas.findByCodigoAsignatura", query = "SELECT a FROM Asignaturas a WHERE a.codigoAsignatura = :codigoAsignatura"),
    @NamedQuery(name = "Asignaturas.findByNombreAsignatura", query = "SELECT a FROM Asignaturas a WHERE a.nombreAsignatura = :nombreAsignatura")})
public class Asignaturas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo_asignatura")
    private String codigoAsignatura;
    @Basic(optional = false)
    @Column(name = "nombre_asignatura")
    private String nombreAsignatura;
    @JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
    @ManyToOne(optional = false)
    private Estados idEstado;

    public Asignaturas() {
    }

    public Asignaturas(String codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public Asignaturas(String codigoAsignatura, String nombreAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getCodigoAsignatura() {
        return codigoAsignatura;
    }

    public void setCodigoAsignatura(String codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public Estados getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Estados idEstado) {
        this.idEstado = idEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigoAsignatura != null ? codigoAsignatura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asignaturas)) {
            return false;
        }
        Asignaturas other = (Asignaturas) object;
        if ((this.codigoAsignatura == null && other.codigoAsignatura != null) || (this.codigoAsignatura != null && !this.codigoAsignatura.equals(other.codigoAsignatura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Asignaturas[ codigoAsignatura=" + codigoAsignatura + " ]";
    }

}
