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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Aaron
 */
@Entity
@Table(name = "horario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Horario.findAll", query = "SELECT h FROM Horario h"),
    @NamedQuery(name = "Horario.findByIdHorario", query = "SELECT h FROM Horario h WHERE h.idHorario = :idHorario"),
    @NamedQuery(name = "Horario.findByCohorte", query = "SELECT h FROM Horario h WHERE h.cohorte = :cohorte"),
    @NamedQuery(name = "Horario.findByGrupo", query = "SELECT h FROM Horario h WHERE h.grupo = :grupo"),
    @NamedQuery(name = "Horario.findByFranja", query = "SELECT h FROM Horario h WHERE h.franja = :franja"),
    @NamedQuery(name = "Horario.findByNombreAsignatura", query = "SELECT h FROM Horario h WHERE h.nombreAsignatura = :nombreAsignatura"),
    @NamedQuery(name = "Horario.findByIntensidad", query = "SELECT h FROM Horario h WHERE h.intensidad = :intensidad"),
    @NamedQuery(name = "Horario.findBySalon", query = "SELECT h FROM Horario h WHERE h.salon = :salon"),
    @NamedQuery(name = "Horario.findByDia", query = "SELECT h FROM Horario h WHERE h.dia = :dia"),
    @NamedQuery(name = "Horario.findByHEntrada", query = "SELECT h FROM Horario h WHERE h.hEntrada = :hEntrada"),
    @NamedQuery(name = "Horario.findByHSalida", query = "SELECT h FROM Horario h WHERE h.hSalida = :hSalida"),
    @NamedQuery(name = "Horario.findByEstado", query = "SELECT h FROM Horario h WHERE h.estado = :estado")})
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_horario")
    private Integer idHorario;
    @Basic(optional = false)
    @Column(name = "cohorte")
    private int cohorte;
    @Basic(optional = false)
    @Column(name = "grupo")
    private int grupo;
    @Basic(optional = false)
    @Column(name = "franja")
    private String franja;
    @Basic(optional = false)
    @Column(name = "nombre_asignatura")
    private String nombreAsignatura;
    @Basic(optional = false)
    @Column(name = "intensidad")
    private String intensidad;
    @Basic(optional = false)
    @Column(name = "salon")
    private String salon;
    @Basic(optional = false)
    @Column(name = "dia")
    private String dia;
    @Basic(optional = false)
    @Column(name = "h_entrada")
    private String hEntrada;
    @Basic(optional = false)
    @Column(name = "h_salida")
    private String hSalida;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;
    @JoinColumn(name = "codasignatura", referencedColumnName = "codasignatura")
    @ManyToOne(optional = false)
    private Asignatura codasignatura;
    @JoinColumn(name = "id_plan", referencedColumnName = "id_plan")
    @ManyToOne(optional = false)
    private Plan idPlan;
    @JoinColumn(name = "u_login", referencedColumnName = "u_login")
    @ManyToOne(optional = false)
    private Usuarios uLogin;
    @Transient
    private String plan;
    @Transient
    private String asignatura;
    @Transient
    private String docente;

    public Horario() {
    }

    public Horario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Horario(Integer idHorario, int cohorte, int grupo, String franja, String nombreAsignatura, String intensidad, String salon, String dia, String hEntrada, String hSalida, int estado) {
        this.idHorario = idHorario;
        this.cohorte = cohorte;
        this.grupo = grupo;
        this.franja = franja;
        this.nombreAsignatura = nombreAsignatura;
        this.intensidad = intensidad;
        this.salon = salon;
        this.dia = dia;
        this.hEntrada = hEntrada;
        this.hSalida = hSalida;
        this.estado = estado;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public int getCohorte() {
        return cohorte;
    }

    public void setCohorte(int cohorte) {
        this.cohorte = cohorte;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setGrupo(int grupo) {
        this.grupo = grupo;
    }

    public String getFranja() {
        return franja;
    }

    public void setFranja(String franja) {
        this.franja = franja;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHEntrada() {
        return hEntrada;
    }

    public void setHEntrada(String hEntrada) {
        this.hEntrada = hEntrada;
    }

    public String getHSalida() {
        return hSalida;
    }

    public void setHSalida(String hSalida) {
        this.hSalida = hSalida;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Asignatura getCodasignatura() {
        return codasignatura;
    }

    public void setCodasignatura(Asignatura codasignatura) {
        this.codasignatura = codasignatura;
    }

    public Plan getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Plan idPlan) {
        this.idPlan = idPlan;
    }

    public Usuarios getULogin() {
        return uLogin;
    }

    public void setULogin(Usuarios uLogin) {
        this.uLogin = uLogin;
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
        if (!(object instanceof Horario)) {
            return false;
        }
        Horario other = (Horario) object;
        if ((this.idHorario == null && other.idHorario != null) || (this.idHorario != null && !this.idHorario.equals(other.idHorario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Horario[ idHorario=" + idHorario + " ]";
    }

    /**
     * @return the plan
     */
    public String getPlan() {
        plan = "" + idPlan;
        return plan;
    }

    /**
     * @param plan the plan to set
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * @return the asignatura
     */
    public String getAsignatura() {
        asignatura = codasignatura.getNombreAsignatura();
        return asignatura;
    }

    /**
     * @param asignatura the asignatura to set
     */
    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    /**
     * @return the docente
     */
    public String getDocente() {
        docente = uLogin.getNombreLogin();
        return docente;
    }

    /**
     * @param docente the docente to set
     */
    public void setDocente(String docente) {
        this.docente = docente;
    }

}
