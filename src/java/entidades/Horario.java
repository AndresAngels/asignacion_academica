/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AndresAngel
 */
@Entity
@Table(name = "horario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Horario.findAll", query = "SELECT h FROM Horario h"),
    @NamedQuery(name = "Horario.findByIdHorario", query = "SELECT h FROM Horario h WHERE h.idHorario = :idHorario"),
    @NamedQuery(name = "Horario.findByIdPlan", query = "SELECT h FROM Horario h WHERE h.idPlan = :idPlan"),
    @NamedQuery(name = "Horario.findByCohorte", query = "SELECT h FROM Horario h WHERE h.cohorte = :cohorte"),
    @NamedQuery(name = "Horario.findByGrupo", query = "SELECT h FROM Horario h WHERE h.grupo = :grupo"),
    @NamedQuery(name = "Horario.findByCodAsignatura", query = "SELECT h FROM Horario h WHERE h.codAsignatura = :codAsignatura"),
    @NamedQuery(name = "Horario.findByFranja", query = "SELECT h FROM Horario h WHERE h.franja = :franja"),
    @NamedQuery(name = "Horario.findByULogin", query = "SELECT h FROM Horario h WHERE h.uLogin = :uLogin"),
    @NamedQuery(name = "Horario.findByIntensidad", query = "SELECT h FROM Horario h WHERE h.intensidad = :intensidad"),
    @NamedQuery(name = "Horario.findBySalon", query = "SELECT h FROM Horario h WHERE h.salon = :salon"),
    @NamedQuery(name = "Horario.findByDia", query = "SELECT h FROM Horario h WHERE h.dia = :dia"),
    @NamedQuery(name = "Horario.findByHEntrada", query = "SELECT h FROM Horario h WHERE h.hEntrada = :hEntrada"),
    @NamedQuery(name = "Horario.findByHSalida", query = "SELECT h FROM Horario h WHERE h.hSalida = :hSalida"),
    @NamedQuery(name = "Horario.findByAnio", query = "SELECT h FROM Horario h WHERE h.anio = :anio"),
    @NamedQuery(name = "Horario.findByPeriodo", query = "SELECT h FROM Horario h WHERE h.periodo = :periodo"),
    @NamedQuery(name = "Horario.findByEstado", query = "SELECT h FROM Horario h WHERE h.estado = :estado")})
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_horario")
    private Integer idHorario;
    @Basic(optional = false)
    @Column(name = "id_plan")
    private int idPlan;
    @Basic(optional = false)
    @Column(name = "cohorte")
    private int cohorte;
    @Basic(optional = false)
    @Column(name = "grupo")
    private int grupo;
    @Basic(optional = false)
    @Column(name = "cod_asignatura")
    private String codAsignatura;
    @Basic(optional = false)
    @Column(name = "franja")
    private String franja;
    @Basic(optional = false)
    @Column(name = "u_login")
    private String uLogin;
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
    @Column(name = "anio")
    private String anio;
    @Basic(optional = false)
    @Column(name = "periodo")
    private String periodo;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;
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

    public Horario(Integer idHorario, int idPlan, int cohorte, int grupo, String codAsignatura, String franja, String uLogin, String intensidad, String salon, String dia, String hEntrada, String hSalida, String anio, String periodo, int estado) {
        this.idHorario = idHorario;
        this.idPlan = idPlan;
        this.cohorte = cohorte;
        this.grupo = grupo;
        this.codAsignatura = codAsignatura;
        this.franja = franja;
        this.uLogin = uLogin;
        this.intensidad = intensidad;
        this.salon = salon;
        this.dia = dia;
        this.hEntrada = hEntrada;
        this.hSalida = hSalida;
        this.anio = anio;
        this.periodo = periodo;
        this.estado = estado;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
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

    public String getCodAsignatura() {
        return codAsignatura;
    }

    public void setCodAsignatura(String codAsignatura) {
        this.codAsignatura = codAsignatura;
    }

    public String getFranja() {
        return franja;
    }

    public void setFranja(String franja) {
        this.franja = franja;
    }

    public String getULogin() {
        return uLogin;
    }

    public void setULogin(String uLogin) {
        this.uLogin = uLogin;
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
        asignatura = codAsignatura;
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
        docente = uLogin;
        return docente;
    }

    /**
     * @param docente the docente to set
     */
    public void setDocente(String docente) {
        this.docente = docente;
    }

}
