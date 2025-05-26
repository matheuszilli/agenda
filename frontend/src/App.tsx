import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout';
import CompanyManagement from './pages/companies/CompanyManagement';
import SubsidiaryManagement from './pages/subsidiaries/SubsidiaryManagement';
import ChairRoomManagement from './pages/chairRooms/ChairRoomManagement';
import ItemManagement from './pages/items/ItemManagement';
import ItemList from './pages/items/ItemList';
import ProfessionalManagement from './pages/professionals/ProfessionalManagement';
import ProfessionalList from './pages/professionals/ProfessionalList';
import AppointmentList from './pages/appointments/AppointmentList';
import AppointmentCalendar from './pages/appointments/AppointmentCalendar';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainLayout />}>
                    <Route index element={<div className="welcome-page">
                        <h1>Bem-vindo ao Sistema de Agenda MVP</h1>
                        <p>Utilize o menu superior para navegar pelo sistema.</p>
                    </div>} />
                    
                    {/* Empresas */}
                    <Route path="empresas" element={<CompanyManagement />} />
                    <Route path="empresas/nova" element={<CompanyManagement />} />
                    <Route path="empresas/:id/editar" element={<CompanyManagement />} />
                    
                    {/* Subsidiárias */}
                    <Route path="subsidiarias" element={<SubsidiaryManagement />} />
                    <Route path="subsidiarias/nova" element={<SubsidiaryManagement />} />
                    <Route path="subsidiarias/:id/editar" element={<SubsidiaryManagement />} />
                    
                    {/* Cadeiras/Salas */}
                    <Route path="chair-rooms" element={<ChairRoomManagement />} />
                    <Route path="chair-rooms/new" element={<ChairRoomManagement />} />
                    <Route path="chair-rooms/:id/edit" element={<ChairRoomManagement />} />
                    
                    {/* Serviços */}
                    <Route path="services" element={<ItemList />} />
                    <Route path="services/new" element={<ItemManagement />} />
                    <Route path="services/:id/edit" element={<ItemManagement />} />
                    
                    {/* Profissionais */}
                    <Route path="professionals" element={<ProfessionalManagement />} />
                    <Route path="professionals/new" element={<ProfessionalManagement />} />
                    <Route path="professionals/:id/edit" element={<ProfessionalManagement />} />
                    
                    {/* Agenda */}
                    <Route path="appointments" element={<AppointmentList />} />
                    <Route path="agenda/:subsidiaryId" element={<AppointmentCalendar />} />
                    <Route path="agenda/:subsidiaryId/novo" element={<AppointmentCalendar />} />
                    <Route path="agenda/editar/:id" element={<AppointmentCalendar />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;