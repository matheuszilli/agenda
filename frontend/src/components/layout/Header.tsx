import { useState } from 'react';
import { Link } from 'react-router-dom';
import './Header.css';
import CompanySelector from '../navigation/CompanySelector';

export default function Header() {
    const [showGestaoSubmenu, setShowGestaoSubmenu] = useState(false);
    const [showAgendaSubmenu, setShowAgendaSubmenu] = useState(false);

    return (
        <header className="main-header">
            <div className="logo">
                <Link to="/" className="logo-link">
                    <h1>Agenda MVP</h1>
                </Link>
            </div>

            <div className="company-selector-container">
                <CompanySelector />
            </div>

            <nav className="main-nav">
                <ul className="menu">
                    <li
                        className="menu-item has-submenu"
                        onMouseEnter={() => setShowGestaoSubmenu(true)}
                        onMouseLeave={() => setShowGestaoSubmenu(false)}
                    >
                        <span className="menu-link">Gestão</span>

                        {showGestaoSubmenu && (
                            <ul className="submenu">
                                <li className="submenu-item">
                                    <Link to="/empresas" className="submenu-link">Empresas</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/subsidiarias" className="submenu-link">Subsidiárias</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/chair-rooms" className="submenu-link">Cadeiras/Salas</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/professionals" className="submenu-link">Profissionais</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/services" className="submenu-link">Serviços</Link>
                                </li>
                            </ul>
                        )}
                    </li>

                    <li 
                        className="menu-item has-submenu"
                        onMouseEnter={() => setShowAgendaSubmenu(true)}
                        onMouseLeave={() => setShowAgendaSubmenu(false)}
                    >
                        <span className="menu-link">Agenda</span>
                        
                        {showAgendaSubmenu && (
                            <ul className="submenu">
                                <li className="submenu-item">
                                    <Link to="/appointments" className="submenu-link">Lista de Agendamentos</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/agenda/calendario" className="submenu-link">Calendário</Link>
                                </li>
                            </ul>
                        )}
                    </li>
                </ul>
            </nav>
        </header>
    );
}