import { useState } from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

export default function Header() {
    const [showGestaoSubmenu, setShowGestaoSubmenu] = useState(false);

    return (
        <header className="main-header">
            <div className="logo">
                <h1>Agenda MVP</h1>
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
                                    <Link to="/empresas" className="submenu-link">Empresa</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/subsidiarias" className="submenu-link">Subsidiária</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/cadeiras" className="submenu-link">Cadeira</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/profissionais" className="submenu-link">Profissional</Link>
                                </li>
                                <li className="submenu-item">
                                    <Link to="/servicos" className="submenu-link">Serviço</Link>
                                </li>
                            </ul>
                        )}
                    </li>

                    <li className="menu-item">
                        <Link to="/agenda" className="menu-link">Agenda</Link>
                    </li>
                </ul>
            </nav>
        </header>
    );
}