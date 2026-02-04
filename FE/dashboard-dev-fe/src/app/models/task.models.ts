export interface TaskRequest {
  projectId: number;
  name: string;
  description?: string;
  deliveryDate?: string;
  info?: string;
  archived?: boolean;
}

export interface TaskResponse {
  id: number;
  projectId: number;
  name: string;
  description?: string;
  createdAt: string;
  deliveryDate?: string;
  owner: string;
  info?: string;
  archived?: boolean;
}

/** Spring Page response */
export interface TaskPageResponse {
  content: TaskResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
