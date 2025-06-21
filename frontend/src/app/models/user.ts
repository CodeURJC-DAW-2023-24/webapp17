import { Post } from './post';
import { Comment } from './comment';

export type Role = 'ADMIN' | 'USER';

export interface User {
  id: number;
  username: string;
  password?: string;
  email: string;
  role: Role;
  posts?: Post[];
  comments?: Comment[];
}
