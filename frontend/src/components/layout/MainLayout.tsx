import { Outlet } from 'react-router-dom';
import Header from './Header';
import './MainLayout.css';

export default function MainLayout() {
    return (
        <div className="app-container">
            <Header />
            <main className="main-content">
                <Outlet />
            </main>
        </div>
    );
}