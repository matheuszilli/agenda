// src/services/chairRoomService.ts
import api from './api';

export interface ChairRoom {
    id?: string;
    name: string;
    subsidiaryId: string;
    description?: string;
    capacity: number;
    roomNumber: string;
}

// Interface para respostas paginadas do Spring
export interface PageResponse<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            sorted: boolean;
            unsorted: boolean;
        };
    };
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    first: boolean;
    empty: boolean;
}

export const chairRoomService = {
    getAll: async (page = 0, size = 10) => {
        const response = await api.get('/api/chair-rooms', {
            params: { page, size }
        });
        return response.data as PageResponse<ChairRoom>;
    },

    getById: async (id: string) => {
        const response = await api.get(`/api/chair-rooms/${id}`);
        return response.data;
    },

    getBySubsidiary: async (subsidiaryId: string) => {
        const response = await api.get(`/api/chair-rooms/subsidiary/${subsidiaryId}`);
        return response.data;
    },

    create: async (chairRoom: ChairRoom) => {
        const response = await api.post('/api/chair-rooms', chairRoom);
        return response.data;
    },

    update: async (id: string, chairRoom: ChairRoom) => {
        const response = await api.put(`/api/chair-rooms/${id}`, chairRoom);
        return response.data;
    },

    delete: async (id: string) => {
        await api.delete(`/api/chair-rooms/${id}`);
    },

    // Busca horários de uma cadeira específica
    getSchedules: async (chairRoomId: string) => {
        const response = await api.get(`/api/chair-room-schedules/chair-room/${chairRoomId}`);
        return response.data;
    },

    // Cria um horário para uma cadeira
    createSchedule: async (schedule: any) => {
        const response = await api.post('/api/chair-room-schedules', schedule);
        return response.data;
    }
};