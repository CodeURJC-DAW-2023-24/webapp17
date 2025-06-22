import { User } from './user';
import { Post } from './post';

export interface Comment {
  id: number;
  userId: number;
  post: Post;
  date: string; 
  content: string;
  username: string;
}
