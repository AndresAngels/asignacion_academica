package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "horarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Horarios.findAll", query = "SELECT h FROM Horarios h"),
    @NamedQuery(name = "Horarios.findByIdHorario", query = "SELECT h FROM Horarios h WHERE h.idHorario = :idHorario"),
    @NamedQuery(name = "Horarios.findByCohorteHorario", query = "SELECT h FROM Horarios h WHERE h.cohorteHorario = :cohorteHorario"),
    @NamedQuery(name = "Horarios.findByGrupoHorario", query = "SELECT h FROM Horarios h WHERE h.grupoHorario = :grupoHorario"),
    @NamedQuery(name = "Horarios.findByIntensidadHorario", query = "SELECT h FROM Horarios h WHERE h.intensidadHorario = :intensidadHorario"),
    @NamedQuery(name = "Horarios.findByDiaHorario", query = "SELECT h FROM Horarios h WHERE h.diaHorario = :diaHorario"),
    @NamedQuery(name = "Horarios.findByHEntradaHorario", query = "SELECT h FROM Horarios h WHERE h.hEntradaHorario = :hEntradaHorario"),
    @NamedQuery(name = "Horarios.findByHSalidaHorario", query = "SELECT h FROM Horarios h WHERE h.hSalidaHorario = :hSalidaHorario")})
public class Horarios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_horario")
    private Integer idHorario;
    @Basic(optional = false)
    @Column(name = "cohorte_horario")
    private int cohorteHorario;
    @Basic(optional = false)
    @Column(name = "grupo_horario")
    private int grupoHorario;
    @Basic(optional = false)
    @Column(name = "intensidad_horario")
    private String intensidadHorario;
    @Basic(optional = false)
    @Column(name = "dia_horario")
    private String diaHorario;
    @Basic(optional = false)
    @Column(name = "h_entrada_horario")
    private String hEntradaHorario;
    @Basic(optional = false)
    @Column(name = "h_salida_horario")
    private String hSalidaHorario;
    @JoinColumn(name = "codigo_asignatura", referencedColumnName = "codigo_asignatura")
    @ManyToOne(optional = false)
    private Asignaturas codigoAsignatura;
    @JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
    @ManyToOne(optional = false)
    private Estados idEstado;
    @JoinColumn(name = "id_modalidad", referencedColumnName = "id_modalidad")
    @ManyToOne(optional = false)
    private Modalidades idModalidad;
    @JoinColumn(name = "id_plan", referencedColumnName = "id_plan")
    @ManyToOne(optional = false)
    private Planes idPlan;
    @JoinColumn(name = "login_usuario", referencedColumnName = "login_usuario")
    @ManyToOne(optional = false)
    private Usuarios loginUsuario;

    public Horarios() {
    }

    public Horarios(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Horarios(Integer idHorario, int cohorteHorario, int grupoHorario, String intensidadHorario, String diaHorario, String hEntradaHorario, String hSalidaHorario) {
        this.idHorario = idHorario;
        this.cohorteHorario = cohorteHorario;
        this.grupoHorario = grupoHorario;
        this.intensidadHorario = intensidadHorario;
        this.diaHorario = diaHorario;
        this.hEntradaHorario = hEntradaHorario;
        this.hSalidaHorario = hSalidaHorario;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public int getCohorteHorario() {
        return cohorteHorario;
    }

    public void setCohorteHorario(int cohorteHorario) {
        this.cohorteHorario = cohorteHorario;
    }

    public int getGrupoHorario() {
        return grupoHorario;
    }

    public void setGrupoHorario(int grupoHorario) {
        this.grupoHorario = grupoHorario;
    }

    public String getIntensidadHorario() {
        return intensidadHorario;
    }

    public void setIntensidadHorario(String intensidadHorario) {
        this.intensidadHorario = intensidadHorario;
    }

    public String getDiaHorario() {
        return diaHorario;
    }

    public void setDiaHorario(String diaHorario) {
        this.diaHorario = diaHorario;
    }

    public String getHEntradaHorario() {
        return hEntradaHorario;
    }

    public void setHEntradaHorario(String hEntradaHorario) {
        this.hEntradaHorario = hEntradaHorario;
    }

    public String getHSalidaHorario() {
        return hSalidaHorario;
    }

    public void setHSalidaHorario(String hSalidaHorario) {
        this.hSalidaHorario = hSalidaHorario;
    }

    public Asignaturas getCodigoAsignatura() {
        return codigoAsignatura;
    }

    public void setCodigoAsignatura(Asignaturas codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public Estados getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Estados idEstado) {
        this.idEstado = idEstado;
    }

    public Modalidades getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(Modalidades idModalidad) {
        this.idModalidad = idModalidad;
    }

    public Planes getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Planes idPlan) {
        this.idPlan = idPlan;
    }

    public Usuarios getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(Usuarios loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idHorario != null ? idHorario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Horarios)) {
            return false;
        }
        Horarios other = (Horarios) object;
        if ((this.idHorario == null && other.idHorario != null) || (this.idHorario != null && !this.idHorario.equals(other.idHorario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Horarios[ idHorario=" + idHorario + " ]";
    }

}
