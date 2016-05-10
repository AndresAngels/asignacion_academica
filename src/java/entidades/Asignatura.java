package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

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
    @Transient
    private String codigo;
    @Transient
    private String nombre;
    @Transient
    private String codigoNombre;

    public Asignatura() {
        //Contructor vacio para limpriar campos
    }

    public Asignatura(String codasignatura) {
        this.codasignatura = codasignatura;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codasignatura != null ? codasignatura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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

    /**
     * @return the codigo
     */
    public String getCodigo() {
        codigo = codasignatura;
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        nombre = nombreAsignatura;
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the codigoNombre
     */
    public String getCodigoNombre() {
        codigoNombre=codasignatura+" - "+nombreAsignatura;
        return codigoNombre;
    }

    /**
     * @param codigoNombre the codigoNombre to set
     */
    public void setCodigoNombre(String codigoNombre) {
        this.codigoNombre = codigoNombre;
    }
}
