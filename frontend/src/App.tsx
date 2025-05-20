import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout';
import CompanyManagement from './pages/companies/CompanyManagement';

const PlaceholderPage = ({ title }: { title: string }) => (
    <div>
        <h1>{title}</h1>
        <p>Esta página ainda será implementada.</p>
    </div>
);

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainLayout />}>
                    <Route index element={<div className="welcome-page">
                        <h1>Bem-vindo ao Sistema de Agenda MVP</h1>
                        <p>Utilize o menu superior para navegar pelo sistema.</p>
                    </div>} />
                    <Route path="empresas" element={<CompanyManagement />} />
                    <Route path="subsidiarias" element={<PlaceholderPage title="Gestão de Subsidiárias" />} />
                    <Route path="cadeiras" element={<PlaceholderPage title="Gestão de Cadeiras" />} />
                    <Route path="profissionais" element={<PlaceholderPage title="Gestão de Profissionais" />} />
                    <Route path="servicos" element={<PlaceholderPage title="Gestão de Serviços" />} />
                    <Route path="agenda" element={<PlaceholderPage title="Agenda" />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;