import { User } from './user';
import { Post } from './post';

export interface Comment {
  user: User;
  post: Post;
  date: string; 
  text: string;
}
